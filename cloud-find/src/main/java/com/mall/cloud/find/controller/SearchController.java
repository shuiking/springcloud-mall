package com.mall.cloud.find.controller;


import com.mall.cloud.find.service.SearchService;
import com.mall.cloud.find.vo.SearchParam;
import com.mall.cloud.find.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping(value = {"/list.html","/"})
    public String getSearchPage(SearchParam searchParam, Model model, HttpServletRequest request) {
        //获取url的数据并封装
        searchParam.set_queryString(request.getQueryString());
        SearchResult result=searchService.getSearchResult(searchParam);
        model.addAttribute("result", result);
        return "list";
    }
}
