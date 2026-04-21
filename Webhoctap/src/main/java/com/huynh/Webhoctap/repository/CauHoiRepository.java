package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.CauHoi;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {

    // Lấy câu hỏi theo quiz
    List<CauHoi> findByQuiz(Quiz quiz);

    // Lấy toàn bộ câu hỏi của giáo viên (từ ngân hàng và từ các quiz)
    @org.springframework.data.jpa.repository.Query("SELECT c FROM CauHoi c WHERE c.giaoVienNganHang = :giaoVien OR c.quiz.giaoVien = :giaoVien")
    List<CauHoi> findTatCaCauHoiCuaGiaoVien(@org.springframework.data.repository.query.Param("giaoVien") NguoiDung giaoVien);

    // Lọc theo chủ đề
    @org.springframework.data.jpa.repository.Query("SELECT c FROM CauHoi c WHERE (c.giaoVienNganHang = :giaoVien OR c.quiz.giaoVien = :giaoVien) AND c.chuDe = :chuDe")
    List<CauHoi> findCauHoiCuaGiaoVienTheoChuDe(@org.springframework.data.repository.query.Param("giaoVien") NguoiDung giaoVien, @org.springframework.data.repository.query.Param("chuDe") String chuDe);

    // Lọc theo độ khó
    @org.springframework.data.jpa.repository.Query("SELECT c FROM CauHoi c WHERE (c.giaoVienNganHang = :giaoVien OR c.quiz.giaoVien = :giaoVien) AND c.doKho = :doKho")
    List<CauHoi> findCauHoiCuaGiaoVienTheoDoKho(@org.springframework.data.repository.query.Param("giaoVien") NguoiDung giaoVien, @org.springframework.data.repository.query.Param("doKho") String doKho);

    // Lọc theo chủ đề VÀ độ khó
    @org.springframework.data.jpa.repository.Query("SELECT c FROM CauHoi c WHERE (c.giaoVienNganHang = :giaoVien OR c.quiz.giaoVien = :giaoVien) AND c.chuDe = :chuDe AND c.doKho = :doKho")
    List<CauHoi> findCauHoiCuaGiaoVienTheoChuDeAndDoKho(@org.springframework.data.repository.query.Param("giaoVien") NguoiDung giaoVien, @org.springframework.data.repository.query.Param("chuDe") String chuDe, @org.springframework.data.repository.query.Param("doKho") String doKho);

    // Admin: xem toàn bộ ngân hàng câu hỏi
    List<CauHoi> findByNguonGoc(String nguonGoc);
}