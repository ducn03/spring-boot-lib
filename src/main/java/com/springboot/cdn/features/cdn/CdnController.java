package com.springboot.cdn.features.cdn;

import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import com.springboot.cdn.features.BaseController;
import com.springboot.cdn.service.cdn.CdnService;
import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.cdn.dto.CdnSearchRequest;
import com.springboot.lib.aop.LogActivity;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.helper.ControllerHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class CdnController extends BaseController {
    private final CdnService cdnService;

    public CdnController(CdnService cdnService) {
        this.cdnService = cdnService;
    }

    @LogActivity
    @GetMapping
    public ResponseEntity<?> getCdnList(CdnSearchRequest request){
        // Lấy thông tin để pagination
        PagingData pagingData = new PagingData();
        pagingData.setPageIndex(request.getPageIndex());
        pagingData.setPageSize(request.getPageSize());

        List<CdnDTO> cdnList = cdnService.getCdnList(request, pagingData);

        return ControllerHelper.success(cdnList, pagingData);
    }

    @LogActivity
    @GetMapping("/domains")
    public ResponseEntity<?> getDomains(){
        List<String> domains = Arrays.stream(EDomain.values())
                .map(EDomain::name)
                .toList();

        return ControllerHelper.success(domains);
    }

    @LogActivity
    @GetMapping("/envs")
    public ResponseEntity<?> getEnvironments(){
        List<String> environments = Arrays.stream(EEnv.values())
                .map(EEnv::name)
                .toList();

        return ControllerHelper.success(environments);
    }

    @LogActivity
    @GetMapping("/{cdnId}")
    public ResponseEntity<?> getCdn(@PathVariable("cdnId") Long cdnId){
        return ControllerHelper.success(cdnService.getCdn(cdnId));
    }

}
