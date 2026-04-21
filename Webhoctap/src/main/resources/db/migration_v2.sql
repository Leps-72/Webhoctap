-- ============================================================
-- Migration V2: Mở rộng bảng CauHoi cho Ngân Hàng Câu Hỏi
-- Chạy script này trên SQL Server trước khi khởi động ứng dụng
-- ============================================================

-- 1. Cho phép MaQuiz nullable (câu hỏi ngân hàng không cần gắn quiz)
ALTER TABLE CauHoi ALTER COLUMN MaQuiz INT NULL;
GO

-- 2. Thêm cột phân loại chủ đề
ALTER TABLE CauHoi ADD ChuDe NVARCHAR(100) NULL;
GO

-- 3. Thêm cột độ khó: 'De' | 'TrungBinh' | 'Kho'
ALTER TABLE CauHoi ADD DoKho NVARCHAR(20) NULL DEFAULT N'TrungBinh';
GO

-- 4. Thêm cột nguồn gốc: 'Quiz' | 'NganHang'
ALTER TABLE CauHoi ADD NguonGoc NVARCHAR(20) NULL DEFAULT N'Quiz';
GO

-- 5. Thêm FK giáo viên tạo câu hỏi (dùng cho ngân hàng)
ALTER TABLE CauHoi ADD MaGiaoVienNH INT NULL;
GO

ALTER TABLE CauHoi ADD CONSTRAINT FK_CauHoi_GiaoVien
    FOREIGN KEY (MaGiaoVienNH) REFERENCES NguoiDung(MaNguoiDung);
GO

-- 6. Cập nhật dữ liệu hiện có: đánh dấu tất cả câu hỏi cũ là 'Quiz'
UPDATE CauHoi SET NguonGoc = N'Quiz' WHERE NguonGoc IS NULL;
GO

PRINT N'Migration V2 hoàn thành thành công.';
GO
