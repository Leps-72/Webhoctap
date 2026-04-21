package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    Optional<NguoiDung> findByEmail(String email);
    boolean existsByEmail(String email);
    List<NguoiDung> findByVaiTro_MaVaiTro(Integer maVaiTro);
    List<NguoiDung> findByTrangThai(Boolean trangThai);
    List<NguoiDung> findByVaiTro_TenVaiTro(String tenVaiTro);
    List<NguoiDung> findTop5ByOrderByNgayTaoDesc();
}