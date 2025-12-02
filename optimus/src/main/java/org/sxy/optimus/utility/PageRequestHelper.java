package org.sxy.optimus.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.sxy.optimus.dto.PageRequestDTO;

import java.util.List;

@Slf4j
public class PageRequestHelper {
    public static Pageable toPageable(PageRequestDTO pageRequestDTO){
        String sortBy = pageRequestDTO.getSortBy();
        String sortOrder = pageRequestDTO.getSortOrder();
        int pageSize = pageRequestDTO.getPageSize();
        int pageNo = pageRequestDTO.getPageNo();

        log.debug("Converting PageRequestDTO to Pageable: pageNo={}, pageSize={}, sortBy={}, sortOrder={}",
                pageNo, pageSize, sortBy, sortOrder);

        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        log.debug("Created Pageable with sort direction: {}", sort.toString());
        return pageable;
    }
}
