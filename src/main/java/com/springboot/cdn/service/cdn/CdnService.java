package com.springboot.cdn.service.cdn;

import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.cdn.dto.CdnSearchRequest;
import com.springboot.lib.dto.PagingData;

import java.util.List;

public interface CdnService {
    List<CdnDTO> getCdnList(CdnSearchRequest request, PagingData page);

    CdnDTO getCdn(Long cdnId);
}
