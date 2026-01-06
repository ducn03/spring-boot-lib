package com.springboot.cdn.service.cdn;

import com.springboot.cdn.exception.AppErrorCodes;
import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.cdn.service.cdn.dto.CdnSearchRequest;
import com.springboot.jpa.domain.Cdn;
import com.springboot.jpa.repository.cdn.CdnRepository;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CdnServiceImpl implements CdnService{

    private final CdnRepository cdnRepository;
    private final CdnMapper cdnMapper;

    public CdnServiceImpl(CdnRepository cdnRepository, CdnMapper cdnMapper) {
        this.cdnRepository = cdnRepository;
        this.cdnMapper = cdnMapper;
    }

    @Override
    public List<CdnDTO> getCdnList(CdnSearchRequest request, PagingData page) {
        Pageable pageable = PageRequest.of(page.getPageIndex(), page.getPageSize());
        Page<Cdn> cdnList = cdnRepository.getFiles(request, pageable);

        page.update(cdnList);
        return cdnMapper.toDTO(cdnList, page);
    }

    @Override
    public CdnDTO getCdn(Long cdnId) {
        if (cdnId == null) {
            throw new AppException(AppErrorCodes.SYSTEM.BAD_REQUEST);
        }

        Optional<Cdn> cdnOptional = cdnRepository.findById(cdnId);
        if (cdnOptional.isEmpty()) {
            throw new AppException(AppErrorCodes.CDN.CDN_NOT_FOUND);
        }

        return cdnMapper.toDTO(cdnOptional.get());
    }
}
