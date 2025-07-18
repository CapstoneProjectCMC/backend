using System;
using System.Text;
using System.Threading.Tasks;
using static System.Net.Mime.MediaTypeNames;
using static System.Net.WebRequestMethods;

namespace FileService.Core.ApiModels
{
    public class AppSettings
    {
        public Jwt Jwt { get; set; }
        public OTP Otp { get; set; }
        public Admin Admin { get; set; }
        public SecretKey SecretKey { get; set; }
        public MongoDbSettings MongoDbSettings { get; set; }
        public FfmpegSettings FfmpegSettings { get; set; }
        public MinioConfig MinioConfig { get; set; }
    }

    public class Jwt
    {
        public string Key { get; set; }
        public string Issuer { get; set; }
        public string Audience { get; set; }
        public int AccessTokenExpiresTime { get; set; }
        public int RefreshTokenExpiresTime { get; set; }
    }

    public class OTP
    {
        public int Expires { get; set; }
        public int OtpResendMax { get; set; }
        public int OtpHoursAvailable { get; set; }
    }

    public class Admin
    {
        public int OtpMaxAttempted { get; set; }
        public int MaxAccessFailedAttempts { get; set; }
        public int OtpExpires { get; set; }
        public int RememberMe { get; set; }
        public int FailAccountLock { get; set; }
        public int GapOtp { get; set; }
    }

    public class SecretKey
    {
        public string Key { get; set; }
    }

    public class MongoDbSettings
    {
        public string ConnectionStrings { get; set; }
        public string DatabaseName { get; set; }
    }

    public class FfmpegSettings
    {
        public string ExecutablePath { get; set; } // Path to the ffmpeg executable
    }

    public class MinioConfig
    {
        public string Endpoint { get; set; }
        public string AccessKey { get; set; }
        public string SecretKey { get; set; }
        public string BucketName { get; set; }
        public bool Secure { get; set; }
    }
}
