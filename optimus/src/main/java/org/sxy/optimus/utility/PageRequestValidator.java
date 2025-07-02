package org.sxy.optimus.utility;

import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.validation.ValidationError;
import org.sxy.optimus.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageRequestValidator {
    public static List<ValidationResult> validatePageRequest(PageRequestDTO pageRequestDTO, List<String>allowedFields){
        Set<String> allowedFieldSet = new HashSet<>(allowedFields);
        List<String>ascSortFields=pageRequestDTO.getAscSortBy();
        List<String>descSortFields=pageRequestDTO.getDescSortBy();

        List<ValidationResult> errors = new ArrayList<ValidationResult>();

        validateSortFields("ascSortBy",ascSortFields,allowedFieldSet,errors);
        validateSortFields("descSortBy",descSortFields,allowedFieldSet,errors);

        return errors;
    }

    private static void validateSortFields(String listName,List<String> sortFields,Set<String> allowedFieldSet,List<ValidationResult> errors){
        int index=0;
        for(String field:sortFields){
            if(!allowedFieldSet.contains(field)){
                ValidationError validationError=new ValidationError(field,"The given field is not valid for sorting in " + listName);
                ValidationResult validationResult=new ValidationResult(index,List.of(validationError));
                errors.add(validationResult);
            }
            index++;
        }
    }
}
