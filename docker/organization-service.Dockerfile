# ====== Build stage ======
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY OrganizationService/OrganizationService.Core/OrganizationService.Core.csproj OrganizationService.Core/
COPY OrganizationService/OrganizationService.DataAccess/OrganizationService.DataAccess.csproj OrganizationService.DataAccess/
COPY OrganizationService/OrganizationService.Service/OrganizationService.Service.csproj OrganizationService.Service/
COPY OrganizationService/OrganizationService.Api/OrganizationService.Api.csproj OrganizationService.Api/

RUN dotnet restore "OrganizationService.Api/OrganizationService.Api.csproj"

# Copy toàn bộ source
COPY OrganizationService/ ./OrganizationService/

# Publish
WORKDIR /src/OrganizationService/OrganizationService.Api
RUN dotnet publish -c Release -o /app/publish

# ====== Runtime stage ======
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=build /app/publish .


ENV ASPNETCORE_URLS=http://+:8082 \
    ASPNETCORE_ENVIRONMENT=docker

EXPOSE 8091
ENTRYPOINT ["dotnet", "OrganizationService.Api.dll"]

