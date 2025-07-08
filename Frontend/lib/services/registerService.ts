import { Department, Stage } from "../types/registerService";
import ApiClient from "./apiClient";

const port = 10011;
const client = new ApiClient(port);

const RegisterService = {
  createDepartment: client.api<
    {
      adminToken: string;
      name: string;
    },
    void
  >("CreateDepartmentMessage"),

  createRegister: client.api<
    {
      adminToken: string;
      studentId: string;
      departmentId: string;
    },
    void
  >("CreateRegisterMessage"),

  createTerm: client.api<
    {
      adminToken: string;
      name: string;
    },
    void
  >("CreateTermMessage"),

  createTrace: client.api<
    {
      adminToken: string;
      studentId: string;
      termId: string;
      stage: Stage;
    },
    void
  >("CreateTraceMessage"),

  queryDepartments: client.api<{ child: void }, Department[]>(
    "QueryDepartmentsMessage"
  ),
};

export default RegisterService;
