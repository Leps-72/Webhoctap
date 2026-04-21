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

    // Lấy toàn bộ ngân hàng câu hỏi của giáo viên
    List<CauHoi> findByNguonGocAndGiaoVienNganHang(String nguonGoc, NguoiDung giaoVien);

    // Lọc ngân hàng theo chủ đề
    List<CauHoi> findByNguonGocAndGiaoVienNganHangAndChuDe(String nguonGoc, NguoiDung giaoVien, String chuDe);

    // Lọc ngân hàng theo độ khó
    List<CauHoi> findByNguonGocAndGiaoVienNganHangAndDoKho(String nguonGoc, NguoiDung giaoVien, String doKho);

    // Lọc ngân hàng theo chủ đề VÀ độ khó
    List<CauHoi> findByNguonGocAndGiaoVienNganHangAndChuDeAndDoKho(
            String nguonGoc, NguoiDung giaoVien, String chuDe, String doKho);

    // Admin: xem toàn bộ ngân hàng câu hỏi
    List<CauHoi> findByNguonGoc(String nguonGoc);
}