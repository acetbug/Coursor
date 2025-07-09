import Result from "@/types/result";

export default class ApiClient {
  constructor(private port: number) {}

  api<T, R>(message: string): (body: T) => Promise<Result<R>> {
    return async (body: T) => {
      const response = await fetch(
        `http://183.172.173.157:${this.port}/api/${message}`,
        {
          method: "POST",
          body: JSON.stringify(body),
        }
      );
      const data = await response.json();
      if (response.ok) return data as R;
      else return new Error(data as string);
    };
  }
}
