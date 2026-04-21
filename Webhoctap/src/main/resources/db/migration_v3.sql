-- ============================================================
-- Migration V3: Lưu lại lịch sử lựa chọn đáp án của học sinh
-- Chạy script này trên SQL Server trước khi khởi động ứng dụng
-- ============================================================

CREATE TABLE ChiTietKetQua (
    MaChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    MaKetQua INT NOT NULL,
    MaCauHoi INT NOT NULL,
    MaLuaChonChon INT NULL,
    CONSTRAINT FK_ChiTietKetQua_KetQua FOREIGN KEY (MaKetQua) REFERENCES KetQuaQuiz(MaKetQua) ON DELETE CASCADE,
    CONSTRAINT FK_ChiTietKetQua_CauHoi FOREIGN KEY (MaCauHoi) REFERENCES CauHoi(MaCauHoi),
    CONSTRAINT FK_ChiTietKetQua_LuaChon FOREIGN KEY (MaLuaChonChon) REFERENCES LuaChon(MaLuaChon)
);
GO

PRINT N'Migration V3 hoàn thành thành công.';
GO
