package org.sxy.optimus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.option.OptionRequestDTO;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.dto.question.QuestionRequestDTO;
import org.sxy.optimus.dto.question.QuestionUpdateReqDTO;
import org.sxy.optimus.dto.quiz.QuizQuestionSequenceDTO;
import org.sxy.optimus.mapper.OptionMapper;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomRepo;
import org.sxy.optimus.validation.ValidationError;
import org.sxy.optimus.validation.ValidationResult;

import java.util.*;

@Service
public class ValidationService {

    private final OptionMapper optionMapper;

    @Autowired
    private QuizRepo quizRepo;

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

    //This method is to validate the quiz is present or not
    public List<ValidationResult> validateQuizIds(List<String> quizIds){
        List<UUID>ids=quizIds.stream()
                .map(UUID::fromString)
                .toList();
        List<UUID>ExistingIds=quizRepo.findExistingIds(ids);
        Set<UUID> existingIds=new HashSet<>(ExistingIds);

        List<ValidationResult> validationResults = new ArrayList<>();

        int index=0;
        for(UUID id : ids){
            if(!existingIds.contains(id)){
                ValidationError validationError=new ValidationError("quizId","This quiz does not exist");
                validationResults.add(new ValidationResult(index,List.of(validationError)));
            }
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

    public List<ValidationResult> validateQuizQuestionSequenceDTO(QuizQuestionSequenceDTO quizQuestionSequenceDTO,List<UUID> existingQuestionIds){
        List<ValidationResult> validationResults = new ArrayList<>();
        List<QuestionPositionDTO> questionsPositions = quizQuestionSequenceDTO.getQuestionsPositions();
        Set<UUID> ids=new HashSet<>(existingQuestionIds);
        Set<UUID> checkedQuestionIds=new HashSet<>();
        Set<Integer> checkedPositions=new HashSet<>();

        int numberOfQuizQuestions=existingQuestionIds.size();

        if(questionsPositions.size()!=numberOfQuizQuestions){
            List<ValidationError> validationError=List.of(new ValidationError("questionsPositions", "number of questions must be equal to number of quiz questions"));
            return List.of(new ValidationResult(0,validationError));
        }

        int index=0;
        for(QuestionPositionDTO questionPositionDTO : questionsPositions){
            List<ValidationError> validationError=new ArrayList<>();
            if(!ids.contains(UUID.fromString(questionPositionDTO.getQuestionId()))){
                validationError.add(new ValidationError("questionId","This question does not exist in quiz"));
            }
            if(checkedQuestionIds.contains(UUID.fromString(questionPositionDTO.getQuestionId()))){
                validationError.add(new ValidationError("questionId","This question is added more than once"));
            }


            if(!(questionPositionDTO.getPosition()>0 && questionPositionDTO.getPosition()<=numberOfQuizQuestions)){
                validationError.add(new ValidationError("position","This question position is out of bound"));
            }else{
                if(checkedPositions.contains(questionPositionDTO.getPosition())){
                    validationError.add(new ValidationError("position","This question position is added more than once"));
                }
                checkedPositions.add(questionPositionDTO.getPosition());
            }
            checkedQuestionIds.add(UUID.fromString(questionPositionDTO.getQuestionId()));
            index++;
            if(!validationError.isEmpty()){
                validationResults.add(new ValidationResult(index,validationError));
            }
        }

        return validationResults;
    }

    public List<ValidationResult> validateQuestionIdsToDelete(List<UUID> questionIdsReq,List<UUID> existingQuestionIds){
        List<ValidationResult> validationResults = new ArrayList<>();
        Set<UUID> ids=new HashSet<>(existingQuestionIds);

       int index=0;
       for(UUID questionId : questionIdsReq){
           List<ValidationError> validationError=new ArrayList<>();
           if(!ids.contains(questionId)){
               validationError.add(new ValidationError("questionId","This question does not exist in quiz"));
           }
           if(!validationError.isEmpty()){
               validationResults.add(new ValidationResult(index,validationError));
           }
           index++;
       }
       return validationResults;
    }

}