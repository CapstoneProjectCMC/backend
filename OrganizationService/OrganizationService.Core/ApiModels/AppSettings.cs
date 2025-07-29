using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.ApiModels
{
    public class AppSettings
    {
        public Jwt Jwt { get; set; }
        public string AllCharacters { get; set; }
        public SecretKey SecretKey { get; set; }
    }

    public class Jwt
    {
        public string Key { get; set; }
        public string Issuer { get; set; }
        public string Audience { get; set; }
        public int AccessTokenExpiresTime { get; set; }
        public int RefreshTokenExpiresTime { get; set; }
    }

    public class SecretKey
    {
        public string Key { get; set; }
    }
}
