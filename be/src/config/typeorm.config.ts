import { ConfigService, ConfigType } from "@nestjs/config";
import { TypeOrmModuleOptions } from "@nestjs/typeorm";
import dbConfig from "./dbConfig";

export class TypeOrmConfigService {
  static createTypeOrmOptions(configService: ConfigService): TypeOrmModuleOptions {
    // TODO: JOI 적용하기
    const config: ConfigType<typeof dbConfig> = configService.get("db");
    return {
      type: "mysql",
      host: config.host,
      port: parseInt(config.port),
      username: config.username,
      password: config.password,
      database: config.database,
      entities: [__dirname + "/../**/*.entity{.ts,.js}"],
      synchronize: true,
    };
  }
}