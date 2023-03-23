--member table 생성
create table members(
    mem_id varchar2(20) not null primary key, name varchar2(15) not null, 
    phone varchar2(20) unique not null, address varchar2(30), email varchar2(45));

--account table 생성
create table accounts(
    acc_id number not null primary key, acc_balance number not null, 
    acc_start date, acc_card varchar(2) not null, 
    mem_id varchar2(20) not null,
    constraint fk_memid foreign key(mem_id) references members(mem_id) on delete cascade
    );

--card table 생성
create table cards(
    card_id number not null primary key,
    card_start date, acc_id number unique not null,
    constraint fk_accid foreign key(acc_id) references accounts(acc_id) on delete cascade);

--history table 생성
create table history(
    acc_id number not null, history_date date not null, history_id number not null,
    kind number not null, price number not null,
    history_balance number not null,
    constraint pk_hist primary key(acc_id, history_date, history_id),
    constraint fk_hist_accid foreign key(acc_id) references accounts(acc_id) on delete cascade);

--시퀀스 생성
create sequence acc_seq
    start with 10000;
    
create sequence card_seq
    start with 100;
    
create sequence hist_seq;

--update card trigger 생성
create or replace trigger trigger_updateCard
    after INSERT OR DELETE on cards
for each row
begin
    IF INSERTING THEN
      update accounts set acc_card='y' where acc_id=:new.acc_id;
    ELSIF DELETING THEN
      update accounts set acc_card='n' where acc_id=:old.acc_id;
    end if;
end;

ALTER TRIGGER trigger_updateCard enable;

select * from cards;
select * from accounts;
select * from members;
drop table accounts cascade constraints;
select * from history;
delete from cards ;
commit;
rollback;

delete from members where email='마포구';



update accounts set acc_balance=50000 where acc_id='10023';
update accounts set acc_balance=40000 where acc_id='10024';

delete from members;

delete from cards;

alter table members add constraint unq_mem_phone unique(phone);

--멤버 추가
insert into members values('900101-1234567', '홍길동', '010-1234-1234', '서울', 'hong@naver.com');
insert into members values('890101-1333421', '김민수', '010-4567-7899', '경기', 'kimms@naver.com');
select * from members order by 1;

select count(*) from members where mem_id='890101-1333421';
    
insert into accounts values(acc_seq.nextval, 1000, sysdate, 'n', '890101-1333421');
insert into accounts values(acc_seq.nextval, 10000, '2023-03-01', 'n', '900101-1234567');

update accounts set acc_card='n' where acc_id='10005';
update accounts set acc_balance=acc_balance-100 where mem_id='900101-1234567';
update accounts set acc_balance=acc_balance+100 where mem_id='890101-1333421';

insert into cards values(card_seq.nextval, '2023-03-03', '10008', 
    (select mem_id
    from accounts
    where accounts.acc_id='10008')
    );

update accounts set acc_balance=acc_balance-100 where acc_id=(
    select acc_id
    from cards
    where card_id=112
);