package org.sxy.optimus.utility;

import lombok.extern.slf4j.Slf4j;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.validation.ValidationError;
import org.sxy.optimus.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class PageRequestValidator {
    public static List<ValidationResult> validatePageRequest(PageRequestDTO pageRequestDTO, List<String>allowedFields){
        Set<String> allowedFieldSet = new HashSet<>(allowedFields);
        String sortBy = pageRequestDTO.getSortBy();
        String sortOrder = pageRequestDTO.getSortOrder();

        log.debug("Validating PageRequestDTO: sortBy={}, sortOrder={}, allowedFields={}",
                sortBy, sortOrder, allowedFields);

        List<ValidationResult> errors = new ArrayList<ValidationResult>();

        validateSortField(sortBy,allowedFieldSet,errors);

        if(!errors.isEmpty()){
            log.warn("Validation failed for PageRequestDTO: sortBy='{}' not in allowed fields {}", sortBy, allowedFields);
        } else {
            log.debug("Validation passed for PageRequestDTO");
        }

        return errors;
    }

    private static void validateSortField(String field,Set<String> allowedFieldSet,List<ValidationResult> errors){
        int index=0;
        if(!allowedFieldSet.contains(field)){
            log.warn("Invalid sort field '{}' - not in allowed set: {}", field, allowedFieldSet);
            ValidationError validationError=new ValidationError(field,"The given field is not valid for sorting" );
            ValidationResult validationResult=new ValidationResult(index,List.of(validationError));
            errors.add(validationResult);
        }

    }
}
