FROM java:openjdk-8-jre
LABEL maintainer="Ashish Ghosh"
ENV PROJ=""
ENV RELEASE=""
ENV TESTSET=""
ENV GRIDURL=""
RUN     mkdir /workspace
WORKDIR /workspace
COPY    . .
RUN chmod -R 755 ./
CMD ./Run.command -run -project_location ${PROJ} -release ${RELEASE} -testset ${TESTSET} -setEnv "run.RemoteGridURL=${GRIDURL}"