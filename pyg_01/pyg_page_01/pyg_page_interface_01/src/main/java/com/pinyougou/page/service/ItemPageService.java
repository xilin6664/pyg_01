package com.pinyougou.page.service;

import java.io.IOException;

public interface ItemPageService {
    void geneItemHtml(Long goodsId) throws IOException, Exception;

    void goodsHtmlAll() throws Exception;
}
