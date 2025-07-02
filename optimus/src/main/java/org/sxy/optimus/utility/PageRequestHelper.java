package org.sxy.optimus.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.sxy.optimus.dto.PageRequestDTO;

import java.util.List;

public class PageRequestHelper {
    public static Pageable toPageable(PageRequestDTO pageRequestDTO){
        Sort sort=buildSort(pageRequestDTO.getAscSortBy(),pageRequestDTO.getDescSortBy());
        return PageRequest.of(pageRequestDTO.getPageNo(),pageRequestDTO.getPageSize(),sort);
    }

    private static Sort buildSort(List<String> ascFields, List<String> descFields){
        Sort sort=Sort.unsorted();
        if(ascFields!=null && !ascFields.isEmpty()){
            for(String ascField : ascFields){
                sort = sort.and(Sort.by(Sort.Direction.ASC, ascField));
            }
        }
        if(descFields!=null && !descFields.isEmpty()){
            for(String descField : descFields){
                sort = sort.and(Sort.by(Sort.Direction.DESC, descField));
            }
        }
        return sort;
    }
}
