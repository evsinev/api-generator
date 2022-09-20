import { useApi, IUseApi, useSend, IUseSend } from "@hooks/useApi";

[#list enums as enum]
export enum [=enum.enumName]  {
    [#list enum.enumValues as value]
    [=value] = "[=value]",
    [/#list]
}

[/#list]

[#list types as type]
export type [=type.typeName] = {
    [#list type.fields as field]
    [=field.fieldName][=field.fieldNullable]: [=field.fieldType];
    [/#list]
}

[/#list]

export default class [=className] {

    //region API
[#list methods as method]
    static [=method.methodName](aRequest : [=method.parameterType]) : Promise<[=method.returnType]> {
        return doPost<[=method.returnType]>("[=method.path]", aRequest);
    }

[/#list]
    //endregion

    //region useEffect
[#list methods as method]
    static useEffect[=method.uppercaseName](aRequest : [=method.parameterType]) : AsyncState<[=method.returnType]> {
        return useEffectAxios<[=method.returnType]>("[=method.path]", aRequest);
    }

[/#list]
    //endregion

}



[#list methods as method]
[#if method.hasParameters()]
export const use[=method.uppercaseName] = (params : [=method.parameterType]) : IUseApi<[=method.returnType]> => {
    return useSend<[=method.returnType]>("[=method.path]", params);
}

export const use[=method.uppercaseName]Effect = (params : [=method.parameterType]) : IUseApi<[=method.returnType]> => {
    return useApi<[=method.returnType]>("[=method.path]", params);
}

[#else]
export const use[=method.uppercaseName] = () : IUseApi<[=method.returnType]> => {
    return useApi<[=method.returnType]>("[=method.path]");
}
[/#if]
[/#list]
