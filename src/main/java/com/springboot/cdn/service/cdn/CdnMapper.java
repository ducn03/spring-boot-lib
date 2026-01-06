package com.springboot.cdn.service.cdn;

import com.springboot.cdn.service.cdn.dto.CdnDTO;
import com.springboot.jpa.domain.Cdn;
import com.springboot.lib.dto.PagingData;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CdnMapper {
    public CdnDTO toDTO(Cdn cdn) {
        if (cdn == null) {
            return null;
        }

        CdnDTO response = new CdnDTO();

        response.setId(cdn.getId());
        response.setDomain(cdn.getDomain());
        response.setEnv(cdn.getEnv());
        response.setGroup(cdn.getGroup());
        response.setLink(cdn.getLink());
        response.setStatus(cdn.getStatus());

        response.setCreatedAt(cdn.getCreatedAt());
        response.setUpdatedAt(cdn.getUpdatedAt());

        return response;
    }

    public List<CdnDTO> toDTO(Page<Cdn> cdnList, PagingData pagingDTO) {
        if (cdnList == null || cdnList.isEmpty()) {
            return null;
        }

        List<CdnDTO> response = new ArrayList<>();

        // Công thức tính first elements
        int firstElement = pagingDTO.getPageIndex() * pagingDTO.getPageSize() + 1;
        for (Cdn cdn : cdnList) {
            CdnDTO dto = toDTO(cdn, firstElement);
            response.add(dto);
            firstElement++;
        }

        return response;
    }

    public CdnDTO toDTO(Cdn cdn, Integer stt) {
        if (cdn == null) {
            return null;
        }

        CdnDTO response = new CdnDTO();

        response.setId(cdn.getId());
        response.setStt(stt);

        response.setDomain(cdn.getDomain());
        response.setEnv(cdn.getEnv());
        response.setGroup(cdn.getGroup());
        response.setLink(cdn.getLink());
        response.setStatus(cdn.getStatus());

        response.setCreatedAt(cdn.getCreatedAt());
        response.setUpdatedAt(cdn.getUpdatedAt());

        return response;
    }
}
