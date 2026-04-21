package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.HocLieu;
import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface HocLieuRepository extends JpaRepository<HocLieu, Integer> {

    List<HocLieu> findByNguoiDung_MaNguoiDung(Integer maNguoiDung);
    List<HocLieu> findByTrangThaiDuyet(String trangThaiDuyet);
    List<HocLieu> findByDaDuyet(Boolean daDuyet);
    List<HocLieu> findByChuDe_MaChuDe(Integer maChuDe);
    List<HocLieu> findByMonHoc_MaMonHoc(Integer maMonHoc);

    List<HocLieu> findByNguoiDung(NguoiDung nguoiDung);
    List<HocLieu> findByKhoaHoc_MaKhoaHocAndDaDuyet(Integer maKhoaHoc, Boolean daDuyet);

    @Modifying
    @Transactional
    @Query("UPDATE HocLieu h SET h.luotXem = h.luotXem + 1 WHERE h.maHocLieu = :maHocLieu")
    void tangLuotXem(@Param("maHocLieu") Integer maHocLieu);

    @Modifying
    @Transactional
    @Query("UPDATE HocLieu h SET h.soLuotTai = h.soLuotTai + 1 WHERE h.maHocLieu = :maHocLieu")
    void tangSoLuotTai(@Param("maHocLieu") Integer maHocLieu);
}