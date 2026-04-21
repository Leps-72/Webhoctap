package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.LuaChon;
import com.huynh.Webhoctap.model.CauHoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface LuaChonRepository extends JpaRepository<LuaChon, Integer> {
    List<LuaChon> findByCauHoi_MaCauHoi(Integer maCauHoi);
    List<LuaChon> findByCauHoi_MaCauHoiAndLaDapAnDung(Integer maCauHoi, Boolean laDapAnDung);
    
    @Modifying
    @Transactional
    void deleteByCauHoi(CauHoi cauHoi);
}