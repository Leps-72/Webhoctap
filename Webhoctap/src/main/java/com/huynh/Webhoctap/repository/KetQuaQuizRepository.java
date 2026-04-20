package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.KetQuaQuiz;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface KetQuaQuizRepository extends JpaRepository<KetQuaQuiz, Integer> {

    List<KetQuaQuiz> findByHocSinh_MaNguoiDung(Integer maHocSinh);
    List<KetQuaQuiz> findByQuiz_MaQuiz(Integer maQuiz);
    Optional<KetQuaQuiz> findTopByHocSinh_MaNguoiDungAndQuiz_MaQuizOrderByDiemDesc(
            Integer maHocSinh, Integer maQuiz);

    @Query("SELECT AVG(k.diem) FROM KetQuaQuiz k WHERE k.quiz.maQuiz = :maQuiz")
    Double tinhDiemTrungBinh(@Param("maQuiz") Integer maQuiz);

    List<KetQuaQuiz> findByHocSinh(NguoiDung hocSinh);
    List<KetQuaQuiz> findByQuiz(Quiz quiz);
    List<KetQuaQuiz> findByQuizAndHocSinh(Quiz quiz, NguoiDung hocSinh);
}