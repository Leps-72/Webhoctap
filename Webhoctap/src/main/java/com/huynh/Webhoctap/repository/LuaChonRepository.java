package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.LuaChon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LuaChonRepository extends JpaRepository<LuaChon, Integer> {
    List<LuaChon> findByCauHoi_MaCauHoi(Integer maCauHoi);
    List<LuaChon> findByCauHoi_MaCauHoiAndLaDapAnDung(Integer maCauHoi, Boolean laDapAnDung);
}