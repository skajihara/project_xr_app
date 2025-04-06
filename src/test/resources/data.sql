INSERT INTO ACCOUNTS (id, name, bio, icon, header_photo, location, birthday, registered, following, follower, valid_flag, delete_flag)
VALUES
('q30387','Shingo Kajihara','最近はVue.jsを学習中です。','/src/assets/icons/user/myicon.svg','/src/assets/images/header/h01.jpg','東京都','1985-05-23','2015-06-15',10,15,1,0),
('user_current','CurrentUser','コーディング中………','/src/assets/icons/user/myicon.svg','/src/assets/images/header/h02.jpg','大阪府','1990-09-12','2018-02-20',20,25,1,0),
('user_A','Test User A','user_aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','/src/assets/icons/user/kkrn_icon_user_12.svg','/src/assets/images/header/h03.jpg','愛知県','1988-04-10','2016-05-17',5,7,1,0),
('user_B','Test User B','user_bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb','/src/assets/icons/user/kkrn_icon_user_14.svg','/src/assets/images/header/h04.jpg','神奈川県','1992-11-01','2017-07-25',3,10,1,0),
('user_C','Test User C','user_cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc','/src/assets/icons/user/kkrn_icon_user_3.svg','/src/assets/images/header/h05.jpg','千葉県','1985-03-13','2011-09-19',8,12,1,0);

INSERT INTO TWEETS (account_id, text, image, likes, retweets, replies, views, datetime, location, delete_flag)
VALUES
('user_A','富山のホタルイカ、最高🍻','/src/assets/images/img02.jpg',9,23,7,14,'2024-03-01 15:30:00','富山県滑川市',0),
('user_B','夜間はライトアップも実施「令和6年度 八女黒木大藤まつり」開催！','/src/assets/images/img03.jpg',301,2,0,124,'2024-03-03 11:23:55','福岡県八女市',0),
('user_C','プレゼントキャンペーン🎁','/src/assets/images/img04.jpg',30133,2322,792,140230,'2024-03-10 00:21:51','東京都江東区',0),
('user_D','ガチャ爆死したなう','/src/assets/images/img01.GIF',13,3,2,140,'2024-03-18 20:10:01','東京都江東区',0),
('user_E','コカ・コーラ 500ml×24本がクーポンと定期お得便で1691円に #広告','/src/assets/images/img01.GIF',23301,232,7333,149934,'2024-03-29 15:30:11',NULL,0);

--自分のツイートデータ
INSERT INTO TWEETS (account_id, text, image, likes, retweets, replies, views, datetime, location, delete_flag)
VALUES
('q30387', '今日はとてもいい天気ですね。', NULL, 45, 10, 5, 300, '2022-02-15 10:30:45', '渋谷区', 0),
('q30387', '新しいカフェがオープンしました。', NULL, 60, 20, 10, 500, '2022-05-01 14:00:00', '港区', 0),
('q30387', '映画を見に行きました。とても面白かったです。', NULL, 80, 25, 15, 700, '2022-07-10 18:45:30', '新宿区', 0),
('q30387', '今日のランチは美味しかった！', NULL, 50, 15, 8, 400, '2022-09-20 12:15:10', '世田谷区', 0),
('q30387', '公園でジョギングしてきました。気持ちよかった。', NULL, 90, 30, 20, 800, '2022-12-05 08:00:00', '品川区', 0);
