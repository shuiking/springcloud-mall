package com.cloud.mall.cloudware.service.impl;

import com.cloud.common.constant.WareConstant;
import com.cloud.mall.cloudware.entity.PurchaseDetailEntity;
import com.cloud.mall.cloudware.service.PurchaseDetailService;
import com.cloud.mall.cloudware.service.WareSkuService;
import com.cloud.mall.cloudware.vo.MergeVo;
import com.cloud.mall.cloudware.vo.PurchaseDoneVo;
import com.cloud.mall.cloudware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.cloudware.dao.PurchaseDao;
import com.cloud.mall.cloudware.entity.PurchaseEntity;
import com.cloud.mall.cloudware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId==null){
            //新增
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurChaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId=purchaseEntity.getId();

        }else{
            List<Long> items = mergeVo.getItems();
            Long finalPurchaseId=purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);

        }
    }

    @Override
    public void receive(List<Long> ids) {
        //1.确认采购单是新建或者以分配的状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurChaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurChaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(i->{
            i.setStatus(WareConstant.PurChaseStatusEnum.RECEIVE.getCode());
            i.setUpdateTime(new Date());
            return i;
        }).collect(Collectors.toList());
        //2.改变采购单状态
        this.updateBatchById(collect);
        //3.改变采购项状态
        collect.forEach((item)->{
            List<PurchaseDetailEntity> entities=purchaseDetailService.listDetailByPurchase(item.getId());
            List<PurchaseDetailEntity> purchaseDetailEntities = entities.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        Long id = purchaseDoneVo.getId();
        //改变采购项状态
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        Boolean flag=true;
        List<PurchaseDetailEntity> update = new ArrayList<>();
        for(PurchaseItemDoneVo item:items){
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if(item.getStatus()==WareConstant.PurChaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else {
                //将成功的采购进行入库
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.FINISH.getCode());
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            update.add(purchaseDetailEntity);

        }
        purchaseDetailService.updateBatchById(update);
        //改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurChaseStatusEnum.FINISH.getCode() : WareConstant.PurChaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}