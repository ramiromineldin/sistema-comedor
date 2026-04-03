import { z } from "zod";

export const SignupRequestSchema = z.object({
  username: z.string().min(1, "Username must not be empty"),
  password: z.string().min(1, "Password must not be empty"), 
  name: z.string().min(1, "Name must not be empty"),
  surname: z.string().min(1, "Surname must not be empty"),
  email: z.string().email("Must be a valid email"),
  age: z.coerce.number().min(1, "Age must be greater than 0"),
  gender: z.string().min(1, "Gender must not be empty"),
  address: z.string().min(1, "Address must not be empty"),
  role: z.string(),
});
export type SignupRequest = z.infer<typeof SignupRequestSchema>;

export const LoginRequestSchema = z.object({
  username: z.string().min(1, "Username must not be empty"),
  password: z.string().min(1, "Password must not be empty"),
  expectedRole: z.string(),
});

export type LoginRequest = z.infer<typeof LoginRequestSchema>;

export const AuthResponseSchema = z.object({
  accessToken: z.string().min(1),
  refreshToken: z.string().min(1),
});

export type AuthResponse = z.infer<typeof AuthResponseSchema>;
