package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.ChiTietKetQua;
import com.huynh.Webhoctap.model.KetQuaQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietKetQuaRepository extends JpaRepository<ChiTietKetQua, Integer> {
    List<ChiTietKetQua> findByKetQuaQuiz(KetQuaQuiz ketQuaQuiz);
}
