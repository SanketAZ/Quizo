package org.sxy.optimus.service;

import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.OptionRequestDTO;
import org.sxy.optimus.dto.QuestionRequestDTO;
import org.sxy.optimus.validation.ValidationError;
import org.sxy.optimus.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValidationService {


    public List<ValidationResult> validateQuestionRequestDTOs(List<QuestionRequestDTO> questionRequestDTOs){
        List<ValidationResult> validationResults = new ArrayList<>();

        int index=0;
        for(QuestionRequestDTO questionRequestDTO : questionRequestDTOs){
            List<ValidationError> questionsError=validateQuestionRequestDTO(questionRequestDTO);
            if(!questionsError.isEmpty()){
                validationResults.add(new ValidationResult(index,questionsError));
            }
            index++;
        }
        return validationResults;
    }

    private List<ValidationError> validateQuestionRequestDTO(QuestionRequestDTO questionRequestDTO){
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if(questionRequestDTO.getOptions().isEmpty() || questionRequestDTO.getOptions().size() == 1){
            errors.add(new ValidationError("options", "Number of options must be greater than 1"));
        }

        if(!isOptionCorrectCount(questionRequestDTO.getOptions(),1)){
            errors.add(new ValidationError("options", "Exactly one correct option is required"));
        }

        if(isDuplicateOptions(questionRequestDTO.getOptions())){
            errors.add(new ValidationError("options", "Duplicate Options present"));
        }
        return errors;
    }

    private boolean isOptionCorrectCount(List<OptionRequestDTO> options, int expectedCount){
        long count=options.stream().filter(OptionRequestDTO::getIsCorrect).count();
        return count == expectedCount;
    }

    private boolean isDuplicateOptions(List<OptionRequestDTO> options){
        Set<OptionRequestDTO> optionsSet= new HashSet<>(options);
        return optionsSet.size() != options.size();
    }

}