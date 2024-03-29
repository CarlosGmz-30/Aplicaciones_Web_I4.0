import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Checkbox, Label, Spinner, TextInput } from "flowbite-react";
import { useFormik } from "formik";
import * as yup from "yup";
import { customAlert } from "../../config/alerts/alert.js";
import AuthContext from "../../config/context/auth-context.js";
import AxiosClient from "../../config/http-client/axios-client.js";

function SigninPage() {
  const { user, dispatch } = useContext(AuthContext);
  const navigate = useNavigate();
  // El formik ayuda a manejar los formularios
  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
    },
    validationSchema: yup.object().shape({
      username: yup.string().required("Campo obligatorio"),
      password: yup.string().required("Campo obligatorio"),
    }),
    onSubmit: async (values, { setSubmitting }) => {
      console.log(values);
      try {
        const response = await AxiosClient({
          url: "/auth/signin",
          method: "POST",
          data: values,
        });
        if (!response.error) {
          /*
                Tienen que validar que rol tiene
                y redireccionarlo
            */
          dispatch({ type: "SIGNIN", payload: response.data });
          navigate("/", { replace: true });
        } else throw Error("Error");
      } catch (error) {
        customAlert(
          "Iniciar sesión",
          "Usuario y/o contraseña incorrectos",
          "Info"
        );
      } finally {
        setSubmitting(false);
      }
    },
  });
  return (
    <>
      <div className="ripple-background">
        <div className="circle xxlarge shade1"></div>
        <div className="circle xlarge shade2"></div>
        <div className="circle large shade3"></div>
        <div className="circle medium shade4"></div>
        <div className="circle small shade5"></div>

        <div
          id="circle-right"
          className="circle-right xxlarge-right shade1"
        ></div>
        <div
          id="circle-right"
          className="circle-right xlarge-right shade2"
        ></div>
        <div
          id="circle-right"
          className="circle-right large-right shade3"
        ></div>
        <div
          id="circle-right"
          className="circle-right medium-right shade4"
        ></div>
        <div
          id="circle-right"
          className="circle-right small-right shade5"
        ></div>
      </div>

      <div id="form-container">
        <form
          id="form"
          className="flex max-w-md flex-col gap-4"
          onSubmit={formik.handleSubmit}
          noValidate
        >
          <h1 id="title">INICIAR SESIÓN</h1>
          <div>
            <div className="mb-2 block">
              <Label htmlFor="email1" value="Correo Electrónico:" />
            </div>
            <TextInput
              id="email1"
              type="email"
              name="username"
              value={formik.values.username}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              helperText={
                formik.errors.username && formik.touched.username ? (
                  <span className="text-red-600">{formik.errors.username}</span>
                ) : null
              }
              placeholder="Correo Electrónico"
              required
            />
          </div>
          <div>
            <div className="mb-2 block">
              <Label htmlFor="password1" value="Contraseña:" />
            </div>
            <TextInput
              id="password1"
              type="password"
              name="password"
              value={formik.values.password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              helperText={
                formik.errors.password && formik.touched.password ? (
                  <span className="text-red-600">{formik.errors.password}</span>
                ) : null
              }
              required
              placeholder="Contraseña"
            />
          </div>
          <div className="flex items-center gap-2">
            <Checkbox id="remember" />
            <Label htmlFor="remember">Recuérdame</Label>
          </div>
          <Button
            id="button"
            type="submit"
            gradientDuoTone="greenToBlue"
            disabled={formik.isSubmitting || !formik.isValid}
          >
            {formik.isSubmitting ? <Spinner /> : <>Iniciar Sesión</>}
          </Button>
        </form>
      </div>
    </>
  );
}

export default SigninPage;
