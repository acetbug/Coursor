import { Department, Stage, Student, Term } from "@/types/registerService";
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

  queryTerms: client.api<{ child: void }, Term[]>("QueryTermsMessage"),

  queryStudent: client.api<{ studentId: string }, Student>(
    "QueryStudentMessage"
  ),

  queryStudents: client.api<{ departmentId: string }, Student[]>(
    "QueryStudentsMessage"
  ),

  updateDepartment: client.api<
    {
      adminToken: string;
      departmentId: string;
      name: string;
    },
    void
  >("UpdateDepartmentMessage"),

  updateTerm: client.api<
    {
      adminToken: string;
      termId: string;
      name: string;
    },
    void
  >("UpdateTermMessage"),

  deleteDepartment: client.api<
    {
      adminToken: string;
      departmentId: string;
    },
    void
  >("DeleteDepartmentMessage"),

  deleteTerm: client.api<
    {
      adminToken: string;
      termId: string;
    },
    void
  >("DeleteTermMessage"),

  deleteRegister: client.api<
    {
      adminToken: string;
      studentId: string;
      departmentId: string;
    },
    void
  >("DeleteRegisterMessage"),

  deleteTrace: client.api<
    {
      adminToken: string;
      studentId: string;
      termId: string;
    },
    void
  >("DeleteTraceMessage"),
};

export default RegisterService;
