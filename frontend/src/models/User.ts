import {z} from "zod";

export const UserSchema = z.object({
  id: z.number().min(1),
  username: z.string().min(1),
  name: z.string().min(1),
  surname: z.string().min(1),
  email: z.string().email(),
  age: z.number().min(1),
  gender: z.string().min(1),
  address: z.string().min(1),
  role: z.string(),
});

export type User = z.infer<typeof UserSchema>;