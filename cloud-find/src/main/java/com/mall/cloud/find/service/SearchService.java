package com.mall.cloud.find.service;


import com.mall.cloud.find.vo.SearchParam;
import com.mall.cloud.find.vo.SearchResult;

public interface SearchService {
    SearchResult getSearchResult(SearchParam searchParam);
}
