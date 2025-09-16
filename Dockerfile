FROM ubuntu:latest
LABEL authors="nkshu"

ENTRYPOINT ["top", "-b"]