--liquibase formatted sql

--changeset Hroniko:3
drop table REPOSITORY cascade constraints ;
drop table ATTRIBUTES cascade constraints;
drop table OBJ_ATTRIBUTES cascade constraints ;
drop table OBJ_TYPES cascade constraints ;
drop table OBJECTS cascade constraints ;
drop table PARAMS cascade constraints ;
drop table REFERENCES cascade constraints ;
drop table DATABASECHANGELOGLOCK cascade constraints ;
drop table DATABASECHANGELOG cascade constraints ;