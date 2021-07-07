FROM openjdk:14-slim

RUN apt-get update &&\
    apt-get install -y curl unzip &&\
    apt-get clean

RUN curl "https://stockfishchess.org/files/stockfish_14_linux_x64.zip" -o /tmp/stockfish.zip &&\
    unzip /tmp/stockfish.zip -d /tmp/ &&\
    ls /tmp/ &&\
    mv /tmp/stockfish_14_linux_x64/stockfish_14_x64 /opt/stockfish &&\
    rm -rf /tmp/stockfish*
