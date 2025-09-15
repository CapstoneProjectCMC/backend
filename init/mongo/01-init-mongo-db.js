(function () {
    const env = (typeof process !== 'undefined' && process.env) ? process.env : {};
    const ROOT_USER = env.MONGODB_ROOT_USER || 'root';
    const ROOT_PWD = env.MONGODB_ROOT_PASSWORD;

    if (!ROOT_PWD) {
        throw new Error('MONGODB_ROOT_PASSWORD is not set in environment');
    }

    const admin = db.getSiblingDB('admin');
    const ok = admin.auth(ROOT_USER, ROOT_PWD);
    if (!ok) {
        throw new Error('Failed to authenticate as root');
    }

    function upsertUser(dbName, user, pass) {
        if (!dbName || !user || !pass) {
            print(`⚠️  Skip invalid params db=${dbName}, user=${user}`);
            return;
        }
        const database = db.getSiblingDB(dbName);
        const existing = database.getUser(user);

        if (existing) {
            database.updateUser(user, {
                pwd: pass,
                roles: [{role: 'readWrite', db: dbName}]
            });
            print(`🔄 Updated user '${user}' on db '${dbName}'`);
        } else {
            database.createUser({
                user: user,
                pwd: pass,
                roles: [{role: 'readWrite', db: dbName}]
            });
            print(`✅ Created user '${user}' on db '${dbName}'`);
        }
    }

    // FILE
    upsertUser(env.FILE_DATABASE, env.FILE_USERNAME, env.FILE_PASSWORD);
    // NOTIFICATION
    upsertUser(env.NOTIFICATION_DATABASE, env.NOTIFICATION_USERNAME, env.NOTIFICATION_PASSWORD);
    // CHAT
    upsertUser(env.CHAT_DATABASE, env.CHAT_USERNAME, env.CHAT_PASSWORD);

    print('👌 Mongo init users done.');
})();