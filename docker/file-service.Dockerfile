# ====== Build stage ======
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY FileService/FileService.Core/FileService.Core.csproj FileService.Core/
COPY FileService/FileService.DataAccess/FileService.DataAccess.csproj FileService.DataAccess/
COPY FileService/FileService.Service/FileService.Service.csproj FileService.Service/
COPY FileService/FileService.Api/FileService.Api.csproj FileService.Api/

RUN dotnet restore "FileService.Api/FileService.Api.csproj"

# Copy toàn bộ source
COPY FileService/ ./FileService/

# Publish
WORKDIR /src/FileService/FileService.Api
RUN dotnet publish -c Release -o /app/publish

# ====== Runtime stage ======
FROM mcr.microsoft.com/dotnet/aspnet:9.0
WORKDIR /app
COPY --from=build /app/publish .

# Cài đặt FFmpeg (bao gồm ffprobe)
RUN apt-get update \
    && apt-get install -y ffmpeg \
    && rm -rf /var/lib/apt/lists/*


ENV ASPNETCORE_URLS=http://+:8082 \
    ASPNETCORE_ENVIRONMENT=docker

EXPOSE 8082
ENTRYPOINT ["dotnet", "FileService.Api.dll"]

