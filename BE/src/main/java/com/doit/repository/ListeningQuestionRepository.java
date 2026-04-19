package com.doit.repository;

import com.doit.entity.ListeningAudio;
import com.doit.entity.ListeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningQuestionRepository extends JpaRepository<ListeningQuestion, Long> {
    
    List<ListeningQuestion> findByAudioIdOrderByOrderNumber(Long audioId);
    
    long countByAudioId(Long audioId);

    List<ListeningQuestion> findByAudioOrderByOrderNumber(ListeningAudio audio);
}
