package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Assignment;
import com.codecampus.quiz.grpc.AssignmentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-12T18:29:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class AssignmentMapperImpl implements AssignmentMapper {

    @Override
    public Assignment toEntity(AssignmentDto dto) {
        if ( dto == null ) {
            return null;
        }

        Assignment.AssignmentBuilder assignment = Assignment.builder();

        assignment.id( dto.getId() );
        assignment.exerciseId( dto.getExerciseId() );
        assignment.studentId( dto.getStudentId() );
        if ( dto.hasDueAt() ) {
            assignment.dueAt( map( dto.getDueAt() ) );
        }
        assignment.completed( dto.getCompleted() );

        return assignment.build();
    }

    @Override
    public AssignmentDto toGrpc(Assignment ent) {
        if ( ent == null ) {
            return null;
        }

        AssignmentDto.Builder assignmentDto = AssignmentDto.newBuilder();

        assignmentDto.setId( ent.getId() );
        assignmentDto.setExerciseId( ent.getExerciseId() );
        assignmentDto.setStudentId( ent.getStudentId() );
        assignmentDto.setDueAt( map( ent.getDueAt() ) );
        assignmentDto.setCompleted( ent.isCompleted() );

        return assignmentDto.build();
    }

    @Override
    public void patch(AssignmentDto assignmentDto, Assignment assignment) {
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
            assignment.setDueAt( map( assignmentDto.getDueAt() ) );
        }
        assignment.setCompleted( assignmentDto.getCompleted() );
    }
}
