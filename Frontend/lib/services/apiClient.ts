export default class ApiClient {
  constructor(private port: number) {}

  api<T, R>(message: string): (body: T) => Promise<R> {
    return async (body: T) => {
      const bodyString = JSON.stringify(body);
      const response = await fetch(
        `http://183.172.173.157:${this.port}/api/${message}`,
        {
          method: "POST",
          body:
            bodyString.slice(0, bodyString.length - 1) +
            ',"planContext":{"traceID":"1234567890","transactionLevel":0}}',
        }
      );
      const data = await response.json();
      if (response.ok) return data as R;
      else throw new Error(data as string);
    };
  }
}
