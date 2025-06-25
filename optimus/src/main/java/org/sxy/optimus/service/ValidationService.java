package org.sxy.optimus.service;

import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.option.OptionRequestDTO;
import org.sxy.optimus.dto.question.QuestionRequestDTO;
import org.sxy.optimus.dto.question.QuestionUpdateReqDTO;
import org.sxy.optimus.mapper.OptionMapper;
import org.sxy.optimus.validation.ValidationError;
import org.sxy.optimus.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValidationService {

    private final OptionMapper optionMapper;

    public ValidationService(OptionMapper optionMapper) {
        this.optionMapper = optionMapper;
    }


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

    public List<ValidationResult> validateQuestionUpdateReqDTOs(List<QuestionUpdateReqDTO> questionUpdateReqDTOs){
        List<ValidationResult> validationResults = new ArrayList<>();

        int index=0;
        for(QuestionUpdateReqDTO questionUpdateReqDTO : questionUpdateReqDTOs){
            List<ValidationError> questionsError=validateQuestionUpdateReqDTO(questionUpdateReqDTO);
            if(!questionsError.isEmpty()){
                validationResults.add(new ValidationResult(index,questionsError));
            }
            index++;
        }
        return validationResults;

    }
    private List<ValidationError> validateQuestionUpdateReqDTO(QuestionUpdateReqDTO questionUpdateReqDTO){
        List<ValidationError> errors = new ArrayList<ValidationError>();

        List<OptionRequestDTO> options=new ArrayList<>();
        if(!questionUpdateReqDTO.getNewOptions().isEmpty()){
            options.addAll(questionUpdateReqDTO.getNewOptions());
        }
        options.addAll(optionMapper.toOptionRequestDTOs(questionUpdateReqDTO.getOptions()));

        if(options.isEmpty() || options.size() == 1){
            errors.add(new ValidationError("options", "Number of options must be greater than 1"));
        }

        if(!isOptionCorrectCount(options,1)){
            errors.add(new ValidationError("options", "Exactly one correct option is required"));
        }

        if(isDuplicateOptions(options)){
            errors.add(new ValidationError("options", "Duplicate Options present"));
        }

        return errors;
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