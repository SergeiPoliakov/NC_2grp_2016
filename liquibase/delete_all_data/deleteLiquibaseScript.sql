--liquibase formatted sql

--changeset Hroniko:2
drop table ATTRIBUTES cascade constraints;
drop table OBJ_ATTRIBUTES cascade constraints ;
drop table OBJ_TYPES cascade constraints ;
drop table OBJECTS cascade constraints ;
drop table PARAMS cascade constraints ;
drop table REFERENCES cascade constraints ;