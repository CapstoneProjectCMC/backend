package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Assignment;
import com.codecampus.quiz.grpc.AssignmentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-17T14:58:45+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class AssignmentMapperImpl implements AssignmentMapper {

    @Override
    public Assignment toAssignmentFromAssignmentDto(AssignmentDto assignmentDto) {
        if ( assignmentDto == null ) {
            return null;
        }

        Assignment.AssignmentBuilder assignment = Assignment.builder();

        assignment.id( assignmentDto.getId() );
        assignment.exerciseId( assignmentDto.getExerciseId() );
        assignment.studentId( assignmentDto.getStudentId() );
        if ( assignmentDto.hasDueAt() ) {
            assignment.dueAt( mapProtobufTimestampToInstant( assignmentDto.getDueAt() ) );
        }
        assignment.completed( assignmentDto.getCompleted() );

        return assignment.build();
    }

    @Override
    public AssignmentDto toAssignmentDtoFromAssignment(Assignment assignment) {
        if ( assignment == null ) {
            return null;
        }

        AssignmentDto.Builder assignmentDto = AssignmentDto.newBuilder();

        assignmentDto.setId( assignment.getId() );
        assignmentDto.setExerciseId( assignment.getExerciseId() );
        assignmentDto.setStudentId( assignment.getStudentId() );
        assignmentDto.setDueAt( mapInstantToProtobufTimestamp( assignment.getDueAt() ) );
        assignmentDto.setCompleted( assignment.isCompleted() );

        return assignmentDto.build();
    }

    @Override
    public void patchAssignmentDtoToAssignment(AssignmentDto assignmentDto, Assignment assignment) {
        if ( assignmentDto == null ) {
            return;
        }

        if ( assignmentDto.getId() != null ) {
            assignment.setId( assignmentDto.getId() );
        }
        if ( assignmentDto.getExerciseId() != null ) {
            assignment.setExerciseId( assignmentDto.getExerciseId() );
        }
        if ( assignmentDto.getStudentId() != null ) {
            assignment.setStudentId( assignmentDto.getStudentId() );
        }
        if ( assignmentDto.hasDueAt() ) {
            assignment.setDueAt( mapProtobufTimestampToInstant( assignmentDto.getDueAt() ) );
        }
        assignment.setCompleted( assignmentDto.getCompleted() );
    }
}
