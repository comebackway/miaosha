CREATE TABLE `miaosha`.`goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(16) COMMENT '商品名称',
  `goods_title` varchar(64) COMMENT '商品标题',
  `goods_img` varchar(64) COMMENT '商品图片',
  `gooos_detail` longtext COMMENT '商品详细介绍',
  `goods_price` decimal(10, 2) COMMENT '商品单价',
  `goods_stock` int(11) COMMENT '商品库存 -1表示无限制',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `miaosha`.`miaosha_goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表Id',
  `goods_id` bigint(20) COMMENT '商品id',
  `miaosha_price` decimal(10, 2) COMMENT '秒杀价',
  `stock_count` int(11) COMMENT '库存数量',
  `start_date` datetime COMMENT '秒杀开始时间',
  `end_date` datetime COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `miaosha`.`order_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) COMMENT '用户id',
  `goods_id` bigint(20) COMMENT '商品id',
  `delivery_addr_id` bigint(20) COMMENT '收货地址',
  `goods_name` varchar(16) COMMENT '冗余过来的商品名称',
  `goods_count` int(11) COMMENT '商品数量',
  `goods_price` decimal(10, 2) COMMENT '商品单价',
  `status` tinyint(4) COMMENT '订单状态：0新建未支付 1已支付 2已发货 3已收货 4已退款 5已完成' ,
  `create_date` datetime COMMENT '订单创建时间',
  `pay_date` datetime COMMENT '支付时间',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `miaosha`.`miaosha_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) COMMENT '用户id',
  `goods_id` bigint(20) COMMENT '商品id',
  `order_id` bigint(20) COMMENT '订单id',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `miaosha`.`User`  (
  `id` bigint(20) NOT NULL COMMENT '用户id，手机号',
  `name` varchar(255) COMMENT '用户名称',
  `password` varchar(32) COMMENT 'MD5( MD5(pass明文 + 固定salt) + salt)',
  `salt` varchar(10) COMMENT '随机salt',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;


INSERT INTO `miaosha`.`goods`(`id`, `goods_name`, `goods_title`, `goods_img`, `gooos_detail`, `goods_price`, `goods_stock`) VALUES (1, 'mate30', 'mate30', '/img/mate30.jpg', '华为华为好棒棒', 10000.00, 999);
INSERT INTO `miaosha`.`goods`(`id`, `goods_name`, `goods_title`, `goods_img`, `gooos_detail`, `goods_price`, `goods_stock`) VALUES (2, 'iphone11', 'iphone11', '/img/iphone11.jpg', '水果水果好垃圾', 1.00, 100);
INSERT INTO `miaosha`.`miaosha_goods`(`id`, `goods_id`, `miaosha_price`, `stock_count`, `start_date`, `end_date`) VALUES (1, 1, 100.00, 10, '2020-05-21 16:28:39', '2020-06-27 16:28:43');
INSERT INTO `miaosha`.`miaosha_goods`(`id`, `goods_id`, `miaosha_price`, `stock_count`, `start_date`, `end_date`) VALUES (2, 2, 1.00, 10, '2020-05-31 16:28:56', '2020-06-26 16:28:59');

