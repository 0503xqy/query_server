drop table if exists api_info;
drop table if exists api_group;
drop table if exists query_node;
drop table if exists query_node_relation;
drop table if exists data_source;
-- api group, 支持多级
create table api_group (
  id int(11) not null primary key auto_increment,
  parent_id int(11) not null,
  group_name varchar(255) not null,
  create_by varchar(64) default null comment '创建人',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default null comment '更新人',
  update_time datetime default null comment '更新时间'
);

-- api info
create table api_info (
  id int(11) not null primary key auto_increment,
  group_id int(11) not null,
  api_name varchar(255) not null,
  api_path varchar(255) not null,
  api_method varchar(255) not null,
  api_type varchar(255) not null, -- api type, e.g. 分页，列表，对象
  api_description varchar(255) not null,
  create_by varchar(64) default null comment '创建人',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default null comment '更新人',
  update_time datetime default null comment '更新时间'
);


create table query_node (
  id int(11) not null primary key auto_increment,
  node_name varchar(255) not null,
  node_type varchar(255) not null, -- node type, e.g. 多行，单行，单列，单值
  sql_content longtext not null,
  params json not null, -- node params, 入参
  script longtext not null, -- 动态执行脚本
  data_source_id int(11) not null, -- 关联数据源
  node_description varchar(255) not null,
  create_by varchar(64) default null comment '创建人',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default null comment '更新人',
  update_time datetime default null comment '更新时间'
);


create table query_node_relation (
  id int(11) not null primary key auto_increment,
  node_id int(11) not null,
  parent_id int(11) not null,
  relation_type varchar(255) not null, -- relation type, e.g. 父，子，兄弟
  create_by varchar(64) default null comment '创建人',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default null comment '更新人',
  update_time datetime default null comment '更新时间'
);

create table data_source (
  id int(11) not null primary key auto_increment,
  name varchar(255) not null,
  type varchar(255) not null, -- data source type, e.g. mysql, oracle, sqlserver, postgresql
  url varchar(255) not null,
  username varchar(255) not null,
  password varchar(255) not null,
  is_default tinyint(1) not null default 0,
  description varchar(255) not null,
  create_by varchar(64) default null comment '创建人',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default null comment '更新人',
  update_time datetime default null comment '更新时间'
)