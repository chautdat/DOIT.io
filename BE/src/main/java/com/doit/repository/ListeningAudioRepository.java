package com.doit.repository;

import com.doit.entity.ListeningAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningAudioRepository extends JpaRepository<ListeningAudio, Long> {
    
    List<ListeningAudio> findByExamIdOrderByPartNumber(Long examId);
}
