import { DataSource, Repository } from "typeorm";
import { AddScheduleDto } from "./dto/add-schedule.dto";
import { ScheduleMetaEntity } from "./entity/schedule-meta.entity";
import { Injectable, InternalServerErrorException } from "@nestjs/common";
import { ulid } from "ulid";
import { UserEntity } from "src/user/entity/user.entity";
import { CategoryEntity } from "src/category/entity/category.entity";

@Injectable()
export class ScheduleMetaRepository extends Repository<ScheduleMetaEntity> {
  constructor(dataSource: DataSource) {
    super(ScheduleMetaEntity, dataSource.createEntityManager());
  }

  async addScheduleMeta(dto: AddScheduleDto, user: UserEntity, category: CategoryEntity): Promise<ScheduleMetaEntity> {
    const { categoryUuid, title, description, startAt, endAt } = dto;

    const startTime = startAt.split("T")[1];
    const endTime = endAt.split("T")[1];

    const scheduleMetadata = this.create({ title, description, startTime, endTime, user, category });

    try {
      await this.save(scheduleMetadata);
      return scheduleMetadata;
    } catch (error) {
      throw new InternalServerErrorException();
    }
  }

  async getAllScheduleByDate(user: UserEntity, date: Date): Promise<ScheduleMetaEntity[]> {
    const founds = await this.createQueryBuilder("schedule_metadata")
      .leftJoinAndSelect("schedule_metadata.children", "schedule")
      .andWhere(":date < schedule.endAt", { date: date })
      .getMany();

    console.log("get All Schedule");
    founds.forEach((e) => console.log(e));
    return founds;
  }
}
