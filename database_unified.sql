-- ============================================
-- 药品仓库管理系统 - 统一数据库表结构
-- 解决表分裂问题，统一使用一套表
-- ============================================

DROP DATABASE IF EXISTS drug_shop;
CREATE DATABASE drug_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE drug_shop;

-- ============================================
-- 【统一】用户权限管理表 (保留 sys_ 系列，删除 admin/role/permission 系列)
-- ============================================

-- 用户表
CREATE TABLE sys_user (
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID（主键）' PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色：管理员/店长/药师/收银员/库管',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块1】用户权限管理-用户表';

-- 角色表
CREATE TABLE sys_role (
    role_id VARCHAR(32) NOT NULL COMMENT '角色ID（主键）' PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块1】用户权限管理-角色表';

-- 权限表
CREATE TABLE sys_permission (
    perm_id VARCHAR(32) NOT NULL COMMENT '权限ID（主键）' PRIMARY KEY,
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(50) NOT NULL COMMENT '权限标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块1】用户权限管理-权限表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    user_id VARCHAR(32) NOT NULL,
    role_id VARCHAR(32) NOT NULL,
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块1】用户权限管理-用户角色关联';

-- 角色权限关联表
CREATE TABLE sys_role_perm (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    role_id VARCHAR(32) NOT NULL,
    perm_id VARCHAR(32) NOT NULL,
    KEY idx_role_id (role_id),
    KEY idx_perm_id (perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块1】用户权限管理-角色权限关联';

-- ============================================
-- 【统一】药品基础信息表 (保留 drug_info，删除 drug 表)
-- 注意：drug_info 包含所有药品基本信息
-- 库存相关的批号、有效期、仓库位置等放在 drug_inventory 表
-- ============================================

CREATE TABLE drug_info (
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID' PRIMARY KEY,
    drug_name VARCHAR(100) NOT NULL COMMENT '药品名称',
    generic_name VARCHAR(100) COMMENT '通用名',
    specification VARCHAR(50) NOT NULL COMMENT '规格',
    unit VARCHAR(10) NOT NULL COMMENT '单位：片/盒/瓶',
    manufacturer VARCHAR(100) NOT NULL COMMENT '生产厂家',
    approval_no VARCHAR(50) NOT NULL COMMENT '批准文号',
    category VARCHAR(20) NOT NULL COMMENT '分类：处方药/OTC/器械/保健品',
    medical_insurance VARCHAR(20) COMMENT '医保类型：甲类/乙类/非医保',
    purchase_price DECIMAL(10,2) COMMENT '采购价',
    retail_price DECIMAL(10,2) COMMENT '零售价',
    status TINYINT DEFAULT 1 COMMENT '状态 1在售 0停售',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块2】药品基础信息管理-药品主表';

-- ============================================
-- 【统一】仓库管理表
-- ============================================

CREATE TABLE warehouse (
    warehouse_id VARCHAR(32) NOT NULL COMMENT '仓库ID' PRIMARY KEY,
    warehouse_code VARCHAR(20) NOT NULL COMMENT '仓库编码(显示用，如W001)',
    warehouse_name VARCHAR(50) NOT NULL COMMENT '仓库名称',
    location VARCHAR(100) COMMENT '地址',
    manager_id VARCHAR(32) COMMENT '负责人ID',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_warehouse_code (warehouse_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块7】仓库管理-仓库档案表';

CREATE TABLE warehouse_location (
    loc_id VARCHAR(32) NOT NULL COMMENT '库位ID' PRIMARY KEY,
    loc_code VARCHAR(50) NOT NULL COMMENT '库位编码(显示用，如A01-01)',
    warehouse_id VARCHAR(32) NOT NULL COMMENT '所属仓库ID',
    zone VARCHAR(50) COMMENT '库区',
    shelf VARCHAR(20) COMMENT '货架',
    level VARCHAR(20) COMMENT '层',
    status VARCHAR(20) DEFAULT '空闲' COMMENT '状态 空闲/占用/锁定',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_warehouse (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块7】仓库管理-库位表';

CREATE TABLE warehouse_transfer (
    transfer_id VARCHAR(32) NOT NULL COMMENT '移库ID' PRIMARY KEY,
    transfer_no VARCHAR(30) NOT NULL COMMENT '移库单号',
    src_ware_id VARCHAR(32) NOT NULL COMMENT '源仓库ID',
    dest_ware_id VARCHAR(32) NOT NULL COMMENT '目标仓库ID',
    inventory_id VARCHAR(32) NOT NULL COMMENT '库存ID',
    transfer_num INT NOT NULL COMMENT '移库数量',
    status VARCHAR(20) DEFAULT '申请中' COMMENT '状态 申请中/已审核/已完成/已驳回',
    operator_id VARCHAR(32) COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_src_ware (src_ware_id),
    KEY idx_dest_ware (dest_ware_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块7】仓库管理-移库记录表';

-- ============================================
-- 【统一】库存管理表
-- ============================================

CREATE TABLE drug_inventory (
    inventory_id VARCHAR(32) NOT NULL COMMENT '库存ID' PRIMARY KEY,
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(50) NOT NULL COMMENT '批号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE NOT NULL COMMENT '有效期',
    stock_num INT DEFAULT 0 NOT NULL COMMENT '库存数量',
    warehouse_id VARCHAR(32) COMMENT '仓库ID',
    location VARCHAR(50) COMMENT '货架位置',
    min_stock INT DEFAULT 0 COMMENT '最低库存预警',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态 正常/近效期/过期/锁定',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_drug_batch (drug_id, batch_no),
    KEY idx_drug (drug_id),
    KEY idx_warehouse (warehouse_id),
    KEY idx_expiry (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块5】库存管理-药品库存表';

-- ============================================
-- 【统一】效期管理表
-- ============================================

CREATE TABLE drug_expiry_remind (
    remind_id VARCHAR(32) NOT NULL COMMENT '提醒ID' PRIMARY KEY,
    inventory_id VARCHAR(32) NOT NULL COMMENT '库存ID',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    threshold_days INT NOT NULL COMMENT '预警天数',
    remain_days INT NOT NULL COMMENT '剩余天数',
    status VARCHAR(20) DEFAULT '未处理' COMMENT '状态 未处理/已处理/已忽略',
    remind_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提醒时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_drug (drug_id),
    KEY idx_inventory (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块8】效期管理-效期预警表';

CREATE TABLE drug_scrap (
    scrap_id VARCHAR(32) NOT NULL COMMENT '报废ID' PRIMARY KEY,
    scrap_no VARCHAR(30) NOT NULL COMMENT '报废单号',
    inventory_id VARCHAR(32) NOT NULL COMMENT '库存ID',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    scrap_num INT NOT NULL COMMENT '报废数量',
    reason VARCHAR(500) NOT NULL COMMENT '报废原因',
    status VARCHAR(20) DEFAULT '申请中' COMMENT '状态 申请中/已审核/已审批/已执行/已驳回',
    operator_id VARCHAR(32) COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_scrap_no (scrap_no),
    KEY idx_drug (drug_id),
    KEY idx_inventory (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块8】效期管理-药品报废表';

-- ============================================
-- 【合并】供应商管理表 (原有)
-- ============================================

CREATE TABLE supplier (
    supplier_id VARCHAR(32) NOT NULL COMMENT '供应商ID' PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    qualification VARCHAR(20) COMMENT '资质状态',
    status TINYINT DEFAULT 1 COMMENT '状态 1合作 0停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块3】供应商管理-供应商档案表';

CREATE TABLE supplier_cooperation (
    record_id VARCHAR(32) NOT NULL COMMENT '记录ID' PRIMARY KEY,
    supplier_id VARCHAR(32) NOT NULL COMMENT '供应商ID',
    content TEXT COMMENT '合作记录',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块3】供应商管理-合作记录表';

CREATE TABLE supplier_reconciliation (
    bill_id VARCHAR(32) NOT NULL COMMENT '账单ID' PRIMARY KEY,
    supplier_id VARCHAR(32) NOT NULL COMMENT '供应商ID',
    cycle VARCHAR(50) NOT NULL COMMENT '对账周期',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '对账金额',
    status VARCHAR(20) DEFAULT '未对账' COMMENT '状态 未对账/已对账/已付款',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块3】供应商管理-对账表';

-- ============================================
-- 【合并】采购管理表 (原有)
-- ============================================

CREATE TABLE purchase_order (
    order_id VARCHAR(32) NOT NULL COMMENT '采购单号' PRIMARY KEY,
    supplier_id VARCHAR(32) NOT NULL COMMENT '供应商ID',
    operator_id VARCHAR(32) NOT NULL COMMENT '制单人ID',
    total_quantity INT NOT NULL COMMENT '总数量',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '总金额',
    status VARCHAR(20) DEFAULT '待审核' COMMENT '状态 待审核/已通过/已驳回/已入库',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_supplier (supplier_id),
    KEY idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块4】采购管理-采购单主表';

CREATE TABLE purchase_item (
    item_id VARCHAR(32) NOT NULL COMMENT '明细ID' PRIMARY KEY,
    order_id VARCHAR(32) NOT NULL COMMENT '采购单号',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    purchase_num INT NOT NULL COMMENT '采购数量',
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '采购单价',
    batch_no VARCHAR(50) COMMENT '批号',
    KEY idx_order (order_id),
    KEY idx_drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块4】采购管理-采购单明细表';

CREATE TABLE purchase_in (
    in_id VARCHAR(32) NOT NULL COMMENT '入库单号' PRIMARY KEY,
    order_id VARCHAR(32) NOT NULL COMMENT '采购单号',
    operator_id VARCHAR(32) NOT NULL COMMENT '操作人ID',
    in_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    KEY idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块4】采购管理-采购入库单';

-- ============================================
-- 【合并】销售管理表 (原有)
-- ============================================

CREATE TABLE member_info (
    member_id VARCHAR(32) NOT NULL COMMENT '会员ID' PRIMARY KEY,
    card_no VARCHAR(30) NOT NULL COMMENT '会员卡号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    points INT DEFAULT 0 COMMENT '积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_card (card_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块6】销售管理-会员表';

CREATE TABLE sales_order (
    order_id VARCHAR(32) NOT NULL COMMENT '销售单号' PRIMARY KEY,
    member_id VARCHAR(32) COMMENT '会员ID',
    cashier_id VARCHAR(32) NOT NULL COMMENT '收银员ID',
    total_num INT NOT NULL COMMENT '总数量',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '应收金额',
    discount DECIMAL(12,2) DEFAULT 0.00 COMMENT '优惠',
    pay_amount DECIMAL(12,2) NOT NULL COMMENT '实收金额',
    pay_type VARCHAR(20) NOT NULL COMMENT '支付方式',
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    status VARCHAR(20) DEFAULT '已完成' COMMENT '状态 已完成/已退货/已取消',
    KEY idx_cashier (cashier_id),
    KEY idx_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块6】销售管理-销售订单主表';

CREATE TABLE sales_item (
    item_id VARCHAR(32) NOT NULL COMMENT '明细ID' PRIMARY KEY,
    order_id VARCHAR(32) NOT NULL COMMENT '销售单号',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(50) NOT NULL COMMENT '批号',
    sale_num INT NOT NULL COMMENT '销售数量',
    sale_price DECIMAL(10,2) NOT NULL COMMENT '销售单价',
    KEY idx_order (order_id),
    KEY idx_drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块6】销售管理-销售明细表';

-- ============================================
-- 【新增】库存盘点表
-- ============================================

CREATE TABLE inventory_check (
    check_id VARCHAR(32) NOT NULL COMMENT '盘点单ID' PRIMARY KEY,
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    system_stock INT COMMENT '系统库存数量',
    actual_stock INT COMMENT '实际库存数量',
    diff_num INT COMMENT '差异数量',
    diff_reason VARCHAR(500) COMMENT '差异原因',
    check_date DATE COMMENT '盘点日期',
    checker_id VARCHAR(32) COMMENT '盘点人ID',
    auditor_id VARCHAR(32) COMMENT '审核人ID',
    audit_status VARCHAR(20) DEFAULT '待审核' COMMENT '审核状态 待审核/已通过/已驳回',
    adjust_status VARCHAR(20) DEFAULT '未调整' COMMENT '库存调整状态 未调整/已调整/无需调整',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_drug (drug_id),
    KEY idx_checker (checker_id),
    KEY idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块5】库存管理-库存盘点表';

-- ============================================
-- 【新增】销售退货表
-- ============================================

CREATE TABLE sales_return (
    return_id VARCHAR(32) NOT NULL COMMENT '退货单ID' PRIMARY KEY,
    original_order_id VARCHAR(32) NOT NULL COMMENT '原销售单号',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(50) NOT NULL COMMENT '批号',
    return_num INT NOT NULL COMMENT '退货数量',
    return_reason VARCHAR(500) COMMENT '退货原因',
    refund_amount DECIMAL(12,2) COMMENT '退款金额',
    status VARCHAR(20) DEFAULT '申请中' COMMENT '状态 申请中/审核中/已完成/已驳回',
    operator_id VARCHAR(32) COMMENT '操作人ID',
    auditor_id VARCHAR(32) COMMENT '审核人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    audit_time DATETIME COMMENT '审核时间',
    KEY idx_original_order (original_order_id),
    KEY idx_drug (drug_id),
    KEY idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块6】销售管理-销售退货表';

-- ============================================
-- 【合并】报表统计表 (原有)
-- ============================================

CREATE TABLE report_stat (
    report_id VARCHAR(32) NOT NULL COMMENT '报表ID' PRIMARY KEY,
    report_type VARCHAR(20) NOT NULL COMMENT '报表类型 库存/销售/退货/经营',
    cycle VARCHAR(20) NOT NULL COMMENT '统计周期 日/月/季/年',
    data TEXT NOT NULL COMMENT '统计数据JSON',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块9】报表统计模块-总表';

CREATE TABLE stock_report (
    id VARCHAR(32) NOT NULL COMMENT '记录ID' PRIMARY KEY,
    report_id VARCHAR(32) NOT NULL COMMENT '报表ID',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    stock_num INT NOT NULL COMMENT '库存数量',
    stock_money DECIMAL(12,2) NOT NULL COMMENT '库存金额',
    KEY idx_report (report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块9】报表统计-库存报表';

CREATE TABLE sale_report (
    id VARCHAR(32) NOT NULL COMMENT '记录ID' PRIMARY KEY,
    report_id VARCHAR(32) NOT NULL COMMENT '报表ID',
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    sale_num INT NOT NULL COMMENT '销售数量',
    sale_money DECIMAL(12,2) NOT NULL COMMENT '销售金额',
    KEY idx_report (report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块9】报表统计-销售报表';

-- ============================================
-- 【合并】价格管理表 (原有)
-- ============================================

CREATE TABLE drug_price (
    price_id VARCHAR(32) NOT NULL COMMENT '价格ID' PRIMARY KEY,
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '采购价',
    retail_price DECIMAL(10,2) NOT NULL COMMENT '零售价',
    promo_price DECIMAL(10,2) COMMENT '促销价',
    promo_start DATETIME COMMENT '促销开始时间',
    promo_end DATETIME COMMENT '促销结束时间',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块10】价格管理-药品价格表';

CREATE TABLE price_approve (
    approve_id VARCHAR(32) NOT NULL COMMENT '审批ID' PRIMARY KEY,
    drug_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    old_price DECIMAL(10,2) NOT NULL COMMENT '原价',
    new_price DECIMAL(10,2) NOT NULL COMMENT '新价',
    apply_user VARCHAR(50) NOT NULL COMMENT '申请人',
    approve_user VARCHAR(50) COMMENT '审批人',
    result TINYINT COMMENT '审批结果 1通过 0拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块10】价格管理-调价审批表';

-- ============================================
-- 【合并】订单查询表 (原有)
-- ============================================

CREATE TABLE order_query (
    query_id VARCHAR(32) NOT NULL COMMENT '查询ID' PRIMARY KEY,
    order_id VARCHAR(32) NOT NULL COMMENT '关联订单号',
    order_type VARCHAR(20) NOT NULL COMMENT '订单类型 采购/销售/入库/出库',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    query_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '查询时间',
    KEY idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块11】订单查询模块';

-- ============================================
-- 【合并】系统日志表 (原有)
-- ============================================

CREATE TABLE sys_log (
    log_id VARCHAR(32) NOT NULL COMMENT '日志ID' PRIMARY KEY,
    module VARCHAR(50) NOT NULL COMMENT '操作模块',
    type VARCHAR(30) NOT NULL COMMENT '操作类型',
    user_id VARCHAR(32) NOT NULL COMMENT '操作人ID',
    username VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    content TEXT NOT NULL COMMENT '操作内容',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_user (user_id),
    KEY idx_module (module),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【模块12】系统日志模块';

-- ============================================
-- 数据迁移示例 (如果需要从旧表迁移数据)
-- ============================================

-- 1. 从 drug 表迁移到 drug_info 表
-- INSERT INTO drug_info (drug_id, drug_name, specification, manufacturer, category, status, create_time)
-- SELECT drug_id, drug_name, spec, manufacturer, category, status, NOW() FROM drug;

-- 2. 从 admin 表迁移到 sys_user 表
-- INSERT INTO sys_user (user_id, username, password, real_name, phone, status, create_time)
-- SELECT admin_id, username, password, real_name, phone, status, NOW() FROM admin;

-- 3. 从 role 表迁移到 sys_role 表
-- INSERT INTO sys_role (role_id, role_name, description)
-- SELECT role_id, role_name, description FROM role;

-- 4. 从 permission 表迁移到 sys_permission 表
-- INSERT INTO sys_permission (perm_id, perm_name, perm_code)
-- SELECT permission_id, permission_name, btn_sign FROM permission;

-- ============================================
-- 初始化测试数据
-- ============================================

-- 插入默认管理员 (密码: admin123)
INSERT INTO sys_user (user_id, username, password, real_name, role, phone, status) VALUES
('1', 'admin', 'admin123', '系统管理员', '管理员', '13800138000', 1),
('2', 'cashier01', '123456', '张收银', '收银员', '13800138010', 1),
('3', 'cashier02', '123456', '李收银', '收银员', '13800138011', 1);

-- 插入默认角色（必须先插入角色，再插入用户角色关联）
INSERT INTO sys_role (role_id, role_name, description) VALUES
('1', '管理员', '拥有所有权限'),
('2', '店长', '门店管理权限'),
('3', '库管', '仓库管理权限'),
('4', '收银员', '销售开单权限'),
('5', '药师', '药品管理权限');

-- 插入用户角色关联（通过 sys_user_role 表管理）
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
('ur001', '1', '1'),  -- admin -> 管理员
('ur002', '2', '4'),  -- cashier01 -> 收银员
('ur003', '3', '4');  -- cashier02 -> 收银员

-- 插入默认权限
INSERT INTO sys_permission (perm_id, perm_name, perm_code) VALUES
('1', '药品管理', 'drug:manage'),
('2', '仓库管理', 'warehouse:manage'),
('3', '库存管理', 'inventory:manage'),
('4', '采购管理', 'purchase:manage'),
('5', '销售管理', 'sales:manage'),
('6', '效期管理', 'expiry:manage'),
('7', '报表统计', 'report:view'),
('8', '系统设置', 'system:config');

-- 插入默认仓库 (使用UUID作为主键，显示编码为W001, W002)
INSERT INTO warehouse (warehouse_id, warehouse_code, warehouse_name, location, status) VALUES
('a001000000000000000000000000001', 'W001', '主仓库', '北京市朝阳区某某路1号', 1),
('a001000000000000000000000000002', 'W002', '分仓库A', '上海市浦东新区某某街2号', 1);

-- 插入库位
INSERT INTO warehouse_location (loc_id, loc_code, warehouse_id, zone, shelf, level, status) VALUES
('b001000000000000000000000000001', 'A01-01', 'a001000000000000000000000000001', 'A区', 'A01', '01', '空闲'),
('b001000000000000000000000000002', 'A01-02', 'a001000000000000000000000000001', 'A区', 'A01', '02', '空闲'),
('b001000000000000000000000000003', 'B01-01', 'a001000000000000000000000000001', 'B区', 'B01', '01', '空闲'),
('b001000000000000000000000000004', 'A01-01', 'a001000000000000000000000000002', 'A区', 'A01', '01', '空闲');

-- 插入示例药品
INSERT INTO drug_info (drug_id, drug_name, generic_name, specification, unit, manufacturer, approval_no, category, medical_insurance, purchase_price, retail_price, status) VALUES
('1', '阿莫西林胶囊', '阿莫西林', '0.25g*24粒', '盒', '华北制药股份有限公司', '国药准字H13022378', '处方药', '甲类', 8.50, 12.00, 1),
('2', '感冒灵颗粒', '复方感冒灵', '10g*9袋', '盒', '广州白云山制药总厂', '国药准字Z44021980', 'OTC', '乙类', 6.00, 9.50, 1),
('3', '维生素C片', '维生素C', '100mg*100片', '瓶', '华中药业股份有限公司', '国药准字H42021869', 'OTC', '甲类', 5.00, 8.00, 1);

-- 插入示例库存
INSERT INTO drug_inventory (inventory_id, drug_id, batch_no, production_date, expiry_date, stock_num, warehouse_id, location, status) VALUES
('inv001', '1', 'B20240101', '2024-01-01', '2026-01-01', 500, 'a001000000000000000000000000001', 'A区-A01-01', '正常'),
('inv002', '2', 'C20240115', '2024-01-15', '2026-01-15', 300, 'a001000000000000000000000000001', 'A区-A01-02', '正常'),
('inv003', '3', 'D20240301', '2024-03-01', '2026-03-01', 1000, 'a001000000000000000000000000002', 'A区-A01-01', '正常');

-- ============================================
-- 【新增】测试数据 - 会员、销售订单、盘点、退货
-- ============================================

-- 插入测试会员
INSERT INTO member_info (member_id, card_no, name, phone, points) VALUES
('m001', 'VIP001', '张三', '13800138001', 150),
('m002', 'VIP002', '李四', '13800138002', 280),
('m003', 'VIP003', '王五', '13800138003', 50);

-- 插入测试销售订单
INSERT INTO sales_order (order_id, member_id, cashier_id, total_num, total_amount, discount, pay_amount, pay_type, order_time, status) VALUES
('so001', 'm001', '1', 3, 36.00, 0.00, 36.00, '微信', '2024-05-08 10:30:00', '已完成'),
('so002', 'm002', '1', 2, 19.00, 0.00, 19.00, '支付宝', '2024-05-08 14:20:00', '已完成'),
('so003', NULL, '1', 1, 12.00, 0.00, 12.00, '现金', '2024-05-09 09:15:00', '已完成'),
('so004', 'm001', '1', 5, 60.00, 5.00, 55.00, '医保', '2024-05-09 16:45:00', '已完成');

-- 插入测试销售明细
INSERT INTO sales_item (item_id, order_id, drug_id, batch_no, sale_num, sale_price) VALUES
('si001', 'so001', '1', 'B20240101', 2, 12.00),
('si002', 'so001', '2', 'C20240115', 1, 12.00),
('si003', 'so002', '2', 'C20240115', 2, 9.50),
('si004', 'so003', '1', 'B20240101', 1, 12.00),
('si005', 'so004', '1', 'B20240101', 3, 12.00),
('si006', 'so004', '3', 'D20240301', 2, 12.00);

-- 插入测试盘点单
INSERT INTO inventory_check (check_id, drug_id, system_stock, actual_stock, diff_num, diff_reason, check_date, checker_id, auditor_id, audit_status, adjust_status) VALUES
('chk001', '1', 500, 498, -2, '可能损耗', '2024-05-08', '1', '1', '已通过', '已调整'),
('chk002', '2', 300, 300, 0, NULL, '2024-05-08', '1', NULL, '待审核', '未调整'),
('chk003', '3', 1000, 995, -5, '包装破损', '2024-05-09', '1', '1', '已通过', '已调整');

-- 插入测试退货单
INSERT INTO sales_return (return_id, original_order_id, drug_id, batch_no, return_num, return_reason, refund_amount, status, operator_id, auditor_id, create_time, audit_time) VALUES
('sr001', 'so001', '1', 'B20240101', 1, '顾客不满意', 12.00, '已完成', '1', '1', '2024-05-08 15:00:00', '2024-05-08 16:00:00'),
('sr002', 'so002', '2', 'C20240115', 1, '药品过期', 9.50, '申请中', '1', NULL, '2024-05-09 10:00:00', NULL);
