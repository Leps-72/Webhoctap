package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.CauHoi;
import com.huynh.Webhoctap.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {

    List<CauHoi> findByQuiz_MaQuiz(Integer maQuiz);
    long countByQuiz_MaQuiz(Integer maQuiz);
    List<CauHoi> findByQuiz(Quiz quiz);
}