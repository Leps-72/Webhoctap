package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    Optional<NguoiDung> findByEmail(String email);
    boolean existsByEmail(String email);
    List<NguoiDung> findByVaiTro_MaVaiTro(Integer maVaiTro);
    List<NguoiDung> findByTrangThai(Boolean trangThai);
    List<NguoiDung> findByVaiTro_TenVaiTro(String tenVaiTro);
    List<NguoiDung> findTop5ByOrderByNgayTaoDesc();

    /**
     * Tìm kiếm người dùng với bộ lọc linh hoạt:
     *  - keyword: tìm theo hoTen HOẶC email (null/blank = bỏ qua)
     *  - tenVaiTro: lọc theo vai trò (null/blank = bỏ qua)
     *  - trangThai: lọc theo trạng thái (null = bỏ qua)
     */
    @Query("""
        SELECT u FROM NguoiDung u
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:tenVaiTro IS NULL OR :tenVaiTro = ''
               OR u.vaiTro.tenVaiTro = :tenVaiTro)
          AND (:trangThai IS NULL
               OR u.trangThai = :trangThai)
        ORDER BY u.ngayTao DESC
        """)
    List<NguoiDung> timKiem(@Param("keyword") String keyword,
                             @Param("tenVaiTro") String tenVaiTro,
                             @Param("trangThai") Boolean trangThai);
}