import { GetServerSidePropsContext } from 'next';
import { serverPost } from '@/libs/server/server-post';
import useQuery from '@/hooks/useQuery';

[#list enums as enum]
export enum [=enum.enumName] {
    [#list enum.enumValues as value]
    [=value] = '[=value]',
    [/#list]
}

[/#list]
[#list types as type]
export type [=type.typeName] = {
    [#list type.fields as field]
    [=field.fieldName][=field.fieldNullable]: [=field.fieldType];
    [/#list]
};

[/#list]

// region server side
// ------------------

[#list methods as method]
export function [=method.methodName]ServerPost(ctx: GetServerSidePropsContext, aRequest: [=method.parameterType]) {
  return serverPost<[=method.returnType]>({ url: '[=method.path]', params: aRequest, req: ctx.req });
}

[/#list]
// endregion

// region client side
// ------------------

[#list methods as method]
export function use[=method.uppercaseName](aRequest: [=method.parameterType], aLastResponse?: [=method.returnType]) {
  return useQuery<[=method.returnType]>({ url: '[=method.path]', params: aRequest, initState: aLastResponse });
}

[/#list]
// endregion