# ====== Build stage ======
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy project files to leverage restore cache
COPY FileService/FileService.Core/FileService.Core.csproj         FileService.Core/
COPY FileService/FileService.DataAccess/FileService.DataAccess.csproj FileService.DataAccess/
COPY FileService/FileService.Service/FileService.Service.csproj   FileService.Service/
COPY FileService/FileService.Api/FileService.Api.csproj           FileService.Api/

RUN dotnet restore "FileService.Api/FileService.Api.csproj"

# Copy full source and publish
COPY FileService/ ./FileService/
WORKDIR /src/FileService/FileService.Api
RUN dotnet publish -c Release -o /app/publish

# ====== Runtime stage ======
FROM mcr.microsoft.com/dotnet/aspnet:8.0

# Install FFmpeg + ffprobe (non-interactive, minimal)
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends ffmpeg ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# A writable temp dir for FFmpeg work files
RUN mkdir -p /tmp/ffmpeg-tmp && chmod -R 777 /tmp/ffmpeg-tmp

WORKDIR /app
COPY --from=build /app/publish .

ENV ASPNETCORE_URLS=http://+:8082 \
    ASPNETCORE_ENVIRONMENT=docker \
    TMPDIR=/tmp

EXPOSE 8082
ENTRYPOINT ["dotnet", "FileService.Api.dll"]
