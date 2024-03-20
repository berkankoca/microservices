1) docker klosörünü başka bir path'e yedekle (docker up çalıştırıldığında oluşturulan volume proje içerisinde görünmesin diye)
2) cmd ile yedeklenen yeni klasörün içine git   
3) docker-compose -f ./db-compose.yml build
4) docker-compose -f ./db-compose.yml -p db-server up *--detach komutu arkaplanda çalışması için eklenebilir*


NOT : init klasörü volume ile containera bağlanıyor, docker ayağa kalktıktan sonra bu init içerisindeki sql ler isim sırasına göre çalışıyor.